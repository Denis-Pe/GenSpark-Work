////////////////////
// MODULE IMPORTS //
////////////////////
mod gui;
mod misc_util;
mod tex2d;
use misc_util::*;
use tex2d::*;

////////////////////
//  OTHER IMPORTS //
////////////////////
use std::collections::HashMap;
use std::fs::read;
use std::ptr::null_mut;
use std::rc::Rc;
use std::sync::atomic::AtomicPtr;
use std::sync::Mutex;

use wgpu::*;
use winit::{
    event::*,
    event_loop::{ControlFlow, EventLoop},
    window::{Window, WindowBuilder},
};
use winit_input_helper::WinitInputHelper;

use jni::objects::{JClass, JString};
use jni::sys::*;
use jni::JNIEnv;

#[macro_use]
extern crate lazy_static;

////////////////////
//    CONSTANTS   //
////////////////////
const WINDOW_TITLE: &str = "Humans versus Goblins!";

struct Textures {
    #[rustfmt::ignore]
    //             name     tex    is-drawn?
    inner: HashMap<String, (Tex2D, bool)>,
    device: Rc<Device>,
    queue: Rc<Queue>,
    format: TextureFormat,
}

lazy_static! {
    static ref IMGS:
        // - mutex to ensure a single access at a time
        // - atomic pointer because Textures is not Sync and Send,
        // which means that it cannot be used inside a Mutex
        Mutex<AtomicPtr<Textures>> = Mutex::new(AtomicPtr::new(null_mut()));

    static ref WINIT_INPUT: Mutex<WinitInputHelper> = Mutex::new(WinitInputHelper::new());
}

#[no_mangle]
pub extern "system" fn Java_main_Main_add_1img(
    env: JNIEnv,
    _class: JClass,
    mut pos_x: jfloat,
    mut pos_y: jfloat,
    mut width: jfloat,
    mut height: jfloat,
    img_filename: JString,
    name: JString,
    is_drawn: jboolean,
) {
    pos_x *= 2.0;
    pos_x -= 1.0;

    pos_y *= -2.0;
    pos_y += 1.0;

    width *= 2.0;
    height *= 2.0;

    let vertices = [
        Vertex {
            pos: [pos_x, pos_y],
            tex_coord: [0.0, 0.0],
        },
        Vertex {
            pos: [pos_x, pos_y - height],
            tex_coord: [0.0, 1.0],
        },
        Vertex {
            pos: [pos_x + width, pos_y - height],
            tex_coord: [1.0, 1.0],
        },
        Vertex {
            pos: [pos_x + width, pos_y],
            tex_coord: [1.0, 0.0],
        },
    ];

    let img_bytes = read::<String>(env.get_string(img_filename).unwrap().into()).unwrap();
    let name_ruststr: String = env.get_string(name).unwrap().into();

    let mut mutx_inner = IMGS.lock().unwrap();
    unsafe {
        let textures = (*mutx_inner.get_mut()).as_mut().unwrap();

        textures.inner.insert(
            name_ruststr,
            (
                Tex2D::new(
                    &textures.device,
                    &textures.queue,
                    &textures.format,
                    vertices,
                    &img_bytes,
                ),
                is_drawn != 0,
            ),
        );
    }
}

#[no_mangle]
pub extern "system" fn Java_main_Main_move_1image(
    env: JNIEnv,
    _class: JClass,
    name: JString,
    mut new_x: jfloat,
    mut new_y: jfloat,
) {
    let name_ruststr: String = env.get_string(name).unwrap().into();

    let mut mutx_inner = IMGS.lock().unwrap();
    unsafe {
        let textures = (*mutx_inner.get_mut()).as_mut().unwrap();

        let texture_opt = textures
            .inner
            .iter()
            .find(|(name, (_tex, _is_drawn))| **name == name_ruststr);

        if let Some((_name, (tex, _is_drawn))) = texture_opt {
            new_x *= 2.0;
            new_x -= 1.0;

            new_y *= -2.0;
            new_y += 1.0;

            let (width, height) = tex.get_dimensions();

            let new_position = [
                [new_x, new_y],
                [new_x, new_y - height],
                [new_x + width, new_y - height],
                [new_x + width, new_y],
            ];

            tex.change_position(new_position);
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_main_Main_clear_1imgs(_env: JNIEnv, _class: JClass) {
    let mut mutx_inner = IMGS.lock().unwrap();
    unsafe {
        (*(*mutx_inner.get_mut())).inner.clear();
    }
}

#[no_mangle]
pub extern "system" fn Java_main_Main_is_1key_1pressed(
    env: JNIEnv,
    _class: JClass,
    key: JString,
) -> bool {
    let mut key_ruststr: String = env.get_string(key).unwrap().into();
    WINIT_INPUT.lock().unwrap().key_pressed(
        string_to_virtual_key_code(&mut key_ruststr).unwrap_or_else(|| panic!("Invalid key given!")),
    )
}

#[no_mangle]
pub extern "system" fn Java_main_Main_is_1key_1released(
    env: JNIEnv,
    _class: JClass,
    key: JString,
) -> bool {
    let mut key_ruststr: String = env.get_string(key).unwrap().into();
    WINIT_INPUT.lock().unwrap().key_released(
        string_to_virtual_key_code(&mut key_ruststr).unwrap_or_else(|| panic!("Invalid key given!")),
    )
}

#[no_mangle]
pub extern "system" fn Java_main_Main_is_1key_1held(env: JNIEnv, _class: JClass, key: JString) -> bool {
    let mut key_ruststr: String = env.get_string(key).unwrap().into();
    WINIT_INPUT.lock().unwrap().key_held(
        string_to_virtual_key_code(&mut key_ruststr).unwrap_or_else(|| panic!("Invalid key given!")),
    )
}

#[no_mangle]
pub extern "system" fn Java_main_Main_run_1game(
    _env: JNIEnv,
    _class: JClass,
    width: jint,
    height: jint,
) {
    env_logger::init();
    let event_loop = EventLoop::new();
    let window = WindowBuilder::new()
        .with_title(WINDOW_TITLE)
        .with_inner_size(winit::dpi::LogicalSize {
            width: width as u32,
            height: height as u32,
        })
        .with_resizable(false)
        .build(&event_loop)
        .unwrap();

    let mut wgpu_state = pollster::block_on(WgpuState::new(&window));

    ////////////////////
    //    INIT IMGS   //
    ////////////////////
    let mut imgs = Textures {
        inner: HashMap::new(),
        device: Rc::clone(&wgpu_state.device),
        queue: Rc::clone(&wgpu_state.queue),
        format: wgpu_state.config.format,
    };

    let mut inner = IMGS.lock().unwrap();
    *inner.get_mut() = &mut imgs;
    drop(inner);

    // let mut gui = Gui::new(&window, &wgpu_state);

    event_loop.run(move |event, _, control_flow| {
        let mut input = WINIT_INPUT.lock().unwrap();
        input.update(&event);
        drop(input);

        match event {
            Event::RedrawRequested(window_id) if window_id == window.id() => {
                match wgpu_state.surface.get_current_texture() {
                    Ok(surface_texture) => {
                        let mut mutx_inner = IMGS.lock().unwrap();
                        unsafe {
                            for (_name, (tex, _drawn)) in (*(*mutx_inner.get_mut()))
                                .inner
                                .iter()
                                .filter(|(_name, (_tex, is_drawn))| *is_drawn)
                            {
                                tex.draw(&surface_texture);
                            }
                            if (*(*mutx_inner.get_mut())).inner.is_empty() {
                                // to avoid errors of trying to present the texture without
                                // anything done to it in case Java is behind of Rust and Rust
                                // tries to draw before Java has added anything to draw
                                return;
                            }
                        }
                        drop(mutx_inner);

                        surface_texture.present();
                    }

                    Err(error) => {
                        wgpu_state.handle_render_result(Err(error), control_flow, &window);
                    }
                }
            }

            Event::MainEventsCleared => {
                window.request_redraw();
            }

            Event::WindowEvent {
                ref event,
                window_id,
            } if window_id == window.id() => match event {
                WindowEvent::CloseRequested => {
                    *control_flow = ControlFlow::Exit;
                }

                WindowEvent::Resized(physical_size) => {
                    wgpu_state.resize_window(*physical_size);
                }

                _ => {}
            },

            _ => {}
        };
    })
}

pub struct WgpuState {
    pub surface: Rc<Surface>,
    pub device: Rc<Device>,
    pub queue: Rc<Queue>,
    pub config: SurfaceConfiguration,
    pub adapter: Rc<Adapter>,
}

impl WgpuState {
    pub async fn new(window: &Window) -> WgpuState {
        let instance = Instance::new(Backends::all());
        let surface = unsafe { instance.create_surface(window) };
        let adapter = instance
            .request_adapter(&RequestAdapterOptions {
                power_preference: PowerPreference::default(),
                force_fallback_adapter: false,
                compatible_surface: Some(&surface),
            })
            .await
            .unwrap();

        let (device, queue) = adapter
            .request_device(
                &DeviceDescriptor {
                    label: None,
                    features: Features::empty(),
                    limits: Limits::default(),
                },
                None,
            )
            .await
            .unwrap();

        let config = SurfaceConfiguration {
            usage: TextureUsages::RENDER_ATTACHMENT,
            format: surface.get_supported_formats(&adapter)[0],
            width: window.inner_size().width,
            height: window.inner_size().height,
            present_mode: PresentMode::Fifo,
        };
        surface.configure(&device, &config);

        Self {
            surface: Rc::new(surface),
            device: Rc::new(device),
            queue: Rc::new(queue),
            config: config,
            adapter: Rc::new(adapter),
        }
    }

    pub fn resize_window(&mut self, new_size: winit::dpi::PhysicalSize<u32>) {
        if new_size.width > 0 && new_size.height > 0 {
            self.config.width = new_size.width;
            self.config.height = new_size.height;
            self.surface.configure(&self.device, &self.config);
        }
    }

    pub fn handle_render_result(
        &mut self,
        result: Result<(), SurfaceError>,
        control_flow: &mut ControlFlow,
        window: &Window,
    ) {
        match result {
            Ok(_) => {}
            Err(SurfaceError::OutOfMemory) => *control_flow = ControlFlow::Exit,
            Err(SurfaceError::Lost) => self.resize_window(window.inner_size()),
            Err(error) => eprintln!("{:?}", error),
        }
    }
}

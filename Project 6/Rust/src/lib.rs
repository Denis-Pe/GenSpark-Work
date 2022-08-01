////////////////////
// MODULE IMPORTS //
////////////////////
mod java_img_handling;
mod java_input_logic;
mod tex2d;
use java_img_handling::*;
use java_input_logic::*;

////////////////////
//  OTHER IMPORTS //
////////////////////
use std::collections::HashMap;
use std::rc::Rc;

use wgpu::*;
use winit::{
    event::*,
    event_loop::{ControlFlow, EventLoop},
    window::{Window, WindowBuilder},
};

use jni::objects::{JClass, JString};
use jni::sys::*;
use jni::JNIEnv;

#[macro_use]
extern crate lazy_static;

/*
These are the contants defined in other files:

WINIT_INPUT: Mutex<WinitInputHelper> found in java_input_logic.rs, used for input
IMGS: Mutex<AtomicPtr<Textures>> found in java_img_handling.rs, used for the textures that are drawn, its field inner is a HashMap
*/

#[no_mangle]
pub extern "system" fn Java_main_Main_run_1game(
    env: JNIEnv,
    _class: JClass,
    title: JString,
    width: jint,
    height: jint,
) {
    env_logger::init();
    let event_loop = EventLoop::new();
    let window = WindowBuilder::new()
        .with_title(env.get_string(title).unwrap())
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

struct WgpuState {
    surface: Rc<Surface>,
    device: Rc<Device>,
    queue: Rc<Queue>,
    config: SurfaceConfiguration,
    _adapter: Rc<Adapter>,
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
            config,
            _adapter: Rc::new(adapter),
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

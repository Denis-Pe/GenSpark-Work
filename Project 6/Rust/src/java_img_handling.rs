use jni::objects::{JClass, JString};
use jni::sys::*;
use jni::JNIEnv;

use std::collections::HashMap;
use std::rc::Rc;

use crate::tex2d::*;

use std::fs::read;

use wgpu::*;

use std::sync::atomic::AtomicPtr;
use std::sync::Mutex;
use std::ptr::null_mut;

pub struct Textures {
    #[rustfmt::skip]
    //             name     tex    is-drawn?
    pub inner: HashMap<String, (Tex2D, bool)>,
    pub device: Rc<Device>,
    pub queue: Rc<Queue>,
    pub format: TextureFormat,
}

lazy_static! {
pub static ref IMGS:
        // - mutex to ensure a single access at a time
        // - atomic pointer because Textures is not Sync and Send,
        // which means that it cannot be used inside a Mutex
        Mutex<AtomicPtr<Textures>> = Mutex::new(AtomicPtr::new(null_mut()));
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
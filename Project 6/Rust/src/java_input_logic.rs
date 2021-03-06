use winit::event::VirtualKeyCode;
use jni::objects::{JClass, JString};
use jni::JNIEnv;

use std::sync::Mutex;

use winit_input_helper::WinitInputHelper;

lazy_static! {
    pub static ref WINIT_INPUT: Mutex<WinitInputHelper> = Mutex::new(WinitInputHelper::new());
}

#[no_mangle]
pub extern "system" fn Java_main_Main_is_1key_1pressed(
    env: JNIEnv,
    _class: JClass,
    key: JString,
) -> bool {
    let mut key_ruststr: String = env.get_string(key).unwrap().into();
    WINIT_INPUT.lock().unwrap().key_pressed(
        string_to_virtual_key_code(&mut key_ruststr)
            .unwrap_or_else(|| panic!("Invalid key given!")),
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
        string_to_virtual_key_code(&mut key_ruststr)
            .unwrap_or_else(|| panic!("Invalid key given!")),
    )
}

#[no_mangle]
pub extern "system" fn Java_main_Main_is_1key_1held(
    env: JNIEnv,
    _class: JClass,
    key: JString,
) -> bool {
    let mut key_ruststr: String = env.get_string(key).unwrap().into();
    WINIT_INPUT.lock().unwrap().key_held(
        string_to_virtual_key_code(&mut key_ruststr)
            .unwrap_or_else(|| panic!("Invalid key given!")),
    )
}

fn string_to_virtual_key_code(key: &mut String) -> Option<VirtualKeyCode> {
    key.make_ascii_lowercase();

    match key.as_str() {
        "1" => Some(VirtualKeyCode::Key1),
        "2" => Some(VirtualKeyCode::Key2),
        "3" => Some(VirtualKeyCode::Key3),
        "4" => Some(VirtualKeyCode::Key4),
        "5" => Some(VirtualKeyCode::Key5),
        "6" => Some(VirtualKeyCode::Key6),
        "7" => Some(VirtualKeyCode::Key7),
        "8" => Some(VirtualKeyCode::Key8),
        "9" => Some(VirtualKeyCode::Key9),
        "0" => Some(VirtualKeyCode::Key0),
        "a" => Some(VirtualKeyCode::A),
        "b" => Some(VirtualKeyCode::B),
        "c" => Some(VirtualKeyCode::C),
        "d" => Some(VirtualKeyCode::D),
        "e" => Some(VirtualKeyCode::E),
        "f" => Some(VirtualKeyCode::F),
        "g" => Some(VirtualKeyCode::G),
        "h" => Some(VirtualKeyCode::H),
        "i" => Some(VirtualKeyCode::I),
        "j" => Some(VirtualKeyCode::J),
        "k" => Some(VirtualKeyCode::K),
        "l" => Some(VirtualKeyCode::L),
        "m" => Some(VirtualKeyCode::M),
        "n" => Some(VirtualKeyCode::N),
        "o" => Some(VirtualKeyCode::O),
        "p" => Some(VirtualKeyCode::P),
        "q" => Some(VirtualKeyCode::Q),
        "r" => Some(VirtualKeyCode::R),
        "s" => Some(VirtualKeyCode::S),
        "t" => Some(VirtualKeyCode::T),
        "u" => Some(VirtualKeyCode::U),
        "v" => Some(VirtualKeyCode::V),
        "w" => Some(VirtualKeyCode::W),
        "x" => Some(VirtualKeyCode::X),
        "y" => Some(VirtualKeyCode::Y),
        "z" => Some(VirtualKeyCode::Z),
        "esc" => Some(VirtualKeyCode::Escape),
        "f1" => Some(VirtualKeyCode::F1),
        "f2" => Some(VirtualKeyCode::F2),
        "f3" => Some(VirtualKeyCode::F3),
        "f4" => Some(VirtualKeyCode::F4),
        "f5" => Some(VirtualKeyCode::F5),
        "f6" => Some(VirtualKeyCode::F6),
        "f7" => Some(VirtualKeyCode::F7),
        "f8" => Some(VirtualKeyCode::F8),
        "f9" => Some(VirtualKeyCode::F9),
        "f10" => Some(VirtualKeyCode::F10),
        "f11" => Some(VirtualKeyCode::F11),
        "f12" => Some(VirtualKeyCode::F12),
        "f13" => Some(VirtualKeyCode::F13),
        "f14" => Some(VirtualKeyCode::F14),
        "f15" => Some(VirtualKeyCode::F15),
        "f16" => Some(VirtualKeyCode::F16),
        "f17" => Some(VirtualKeyCode::F17),
        "f18" => Some(VirtualKeyCode::F18),
        "f19" => Some(VirtualKeyCode::F19),
        "f20" => Some(VirtualKeyCode::F20),
        "f21" => Some(VirtualKeyCode::F21),
        "f22" => Some(VirtualKeyCode::F22),
        "f23" => Some(VirtualKeyCode::F23),
        "f24" => Some(VirtualKeyCode::F24),
        "snapshot" | "printscr" | "sysrq" => Some(VirtualKeyCode::Snapshot),
        "scrolllock" => Some(VirtualKeyCode::Scroll),
        "pause_break" => Some(VirtualKeyCode::Pause),
        "ins" | "insert" => Some(VirtualKeyCode::Insert),
        "home" => Some(VirtualKeyCode::Home),
        "delete" => Some(VirtualKeyCode::Delete),
        "end" => Some(VirtualKeyCode::End),
        "pgdn" | "page_down" => Some(VirtualKeyCode::PageDown),
        "pgup" | "page_up" => Some(VirtualKeyCode::PageUp),
        "left" => Some(VirtualKeyCode::Left),
        "up" => Some(VirtualKeyCode::Up),
        "right" => Some(VirtualKeyCode::Right),
        "down" => Some(VirtualKeyCode::Down),
        "back" | "backspace" => Some(VirtualKeyCode::Back),
        "return" | "enter" => Some(VirtualKeyCode::Return),
        "space" => Some(VirtualKeyCode::Space),
        "compose" => Some(VirtualKeyCode::Compose),
        "caret" => Some(VirtualKeyCode::Caret),
        "numlock" => Some(VirtualKeyCode::Numlock),
        "numpad1" => Some(VirtualKeyCode::Numpad1),
        "numpad2" => Some(VirtualKeyCode::Numpad2),
        "numpad3" => Some(VirtualKeyCode::Numpad3),
        "numpad4" => Some(VirtualKeyCode::Numpad4),
        "numpad5" => Some(VirtualKeyCode::Numpad5),
        "numpad6" => Some(VirtualKeyCode::Numpad6),
        "numpad7" => Some(VirtualKeyCode::Numpad7),
        "numpad8" => Some(VirtualKeyCode::Numpad8),
        "numpad9" => Some(VirtualKeyCode::Numpad9),
        "numpad0" => Some(VirtualKeyCode::Numpad0),
        "numpad_add" => Some(VirtualKeyCode::NumpadAdd),
        "numpad_divide" => Some(VirtualKeyCode::NumpadDivide),
        "numpad_decimal" => Some(VirtualKeyCode::NumpadDecimal),
        "numpad_comma" => Some(VirtualKeyCode::NumpadComma),
        "numpad_enter" => Some(VirtualKeyCode::NumpadEnter),
        "numpad_equals" => Some(VirtualKeyCode::NumpadEquals),
        "numpad_multiply" => Some(VirtualKeyCode::NumpadMultiply),
        "numpad_subtract" => Some(VirtualKeyCode::NumpadSubtract),
        "abntc1" => Some(VirtualKeyCode::AbntC1),
        "abntc2" => Some(VirtualKeyCode::AbntC2),
        "apostrophe" => Some(VirtualKeyCode::Apostrophe),
        "apps" => Some(VirtualKeyCode::Apps),
        "asterisk" => Some(VirtualKeyCode::Asterisk),
        "at" => Some(VirtualKeyCode::At),
        "ax" => Some(VirtualKeyCode::Ax),
        "backslash" => Some(VirtualKeyCode::Backslash),
        "calculator" => Some(VirtualKeyCode::Calculator),
        "capital" => Some(VirtualKeyCode::Capital),
        "colon" => Some(VirtualKeyCode::Colon),
        "comma" => Some(VirtualKeyCode::Comma),
        "convert" => Some(VirtualKeyCode::Convert),
        "equals" => Some(VirtualKeyCode::Equals),
        "grave" => Some(VirtualKeyCode::Grave),
        "kana" => Some(VirtualKeyCode::Kana),
        "kanji" => Some(VirtualKeyCode::Kanji),
        "lwin" => Some(VirtualKeyCode::LWin),
        "mail" => Some(VirtualKeyCode::Mail),
        "media_select" => Some(VirtualKeyCode::MediaSelect),
        "media_stop" => Some(VirtualKeyCode::MediaStop),
        "minus" => Some(VirtualKeyCode::Minus),
        "mute" => Some(VirtualKeyCode::Mute),
        "my_computer" | "my_pc" => Some(VirtualKeyCode::MyComputer),
        "navforward" | "nav_forward" => Some(VirtualKeyCode::NavigateForward),
        "navback" | "nav_backward" => Some(VirtualKeyCode::NavigateBackward),
        "next_track" => Some(VirtualKeyCode::NextTrack),
        "no_convert" => Some(VirtualKeyCode::NoConvert),
        "oem102" => Some(VirtualKeyCode::OEM102),
        "period" => Some(VirtualKeyCode::Period),
        "playpause" | "play_pause" | "play" => Some(VirtualKeyCode::PlayPause),
        "plus" => Some(VirtualKeyCode::Plus),
        "power" => Some(VirtualKeyCode::Power),
        "prev_track" | "prev" => Some(VirtualKeyCode::PrevTrack),
        "rwin" => Some(VirtualKeyCode::RWin),
        "semicolon" => Some(VirtualKeyCode::Semicolon),
        "slash" => Some(VirtualKeyCode::Slash),
        "sleep" => Some(VirtualKeyCode::Sleep),
        "stop" => Some(VirtualKeyCode::Stop),
        "tab" => Some(VirtualKeyCode::Tab),
        "underline" => Some(VirtualKeyCode::Underline),
        "unlabeled" => Some(VirtualKeyCode::Unlabeled),
        "volume_down" => Some(VirtualKeyCode::VolumeDown),
        "volume_up" => Some(VirtualKeyCode::VolumeUp),
        "wake" => Some(VirtualKeyCode::Wake),
        "web_back" => Some(VirtualKeyCode::WebBack),
        "web_favorites" => Some(VirtualKeyCode::WebFavorites),
        "web_forward" => Some(VirtualKeyCode::WebForward),
        "web_home" => Some(VirtualKeyCode::WebHome),
        "web_refresh" => Some(VirtualKeyCode::WebRefresh),
        "web_search" => Some(VirtualKeyCode::WebSearch),
        "web_stop" => Some(VirtualKeyCode::WebStop),
        "yen" => Some(VirtualKeyCode::Yen),
        "copy" => Some(VirtualKeyCode::Copy),
        "paste" => Some(VirtualKeyCode::Paste),
        "cut" => Some(VirtualKeyCode::Cut),
        _ => None,
    }
}

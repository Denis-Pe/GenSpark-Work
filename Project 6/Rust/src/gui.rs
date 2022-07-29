use crate::WgpuState;

use imgui::*;
use imgui_wgpu::*;
use imgui_winit_support::*;

use wgpu::*;

use std::rc::Rc;

pub struct Gui {
    device: Rc<Device>,
    queue: Rc<Queue>,
    context: Context,
    platform: WinitPlatform,
    renderer: Renderer,
    last_cursor: Option<MouseCursor>,
}

impl Gui {
    pub fn new(window: &winit::window::Window, wgpu_state: &WgpuState) -> Self {
        let mut context = Context::create();
        let mut platform = WinitPlatform::init(&mut context);

        platform.attach_window(context.io_mut(), window, HiDpiMode::Default);

        context.set_ini_filename(None);

        let hidpi_factor = window.scale_factor();

        let font_size = (13.0 * hidpi_factor) as f32;
        context.io_mut().font_global_scale = (1.0 / hidpi_factor) as f32;

        context.fonts().add_font(&[FontSource::DefaultFontData {
            config: Some(FontConfig {
                size_pixels: font_size,
                oversample_h: 1,
                pixel_snap_h: true,
                ..Default::default()
            }),
        }]);

        let renderer_config = RendererConfig {
            texture_format: wgpu_state.config.format,
            ..Default::default()
        };

        let renderer = Renderer::new(
            &mut context,
            wgpu_state.device.as_ref(),
            wgpu_state.queue.as_ref(),
            renderer_config,
        );

        Self {
            device: Rc::clone(&wgpu_state.device),
            queue: Rc::clone(&wgpu_state.queue),
            context,
            platform,
            renderer,
            last_cursor: None,
        }
    }

    pub fn draw(&mut self, window: &winit::window::Window, surface_texture: &SurfaceTexture) {
        self.platform
            .prepare_frame(self.context.io_mut(), &window)
            .expect("Fatal error: failed to prepare frame");

        let ui = self.context.frame();

        {
            let left_panel = Window::new("is it you?!");
            left_panel
                .title_bar(false)
                .position([0.0, 0.0], Condition::Always)
                .size(
                    [200.0, window.inner_size().height as f32],
                    Condition::Always,
                )
                .movable(false)
                .resizable(false)
                .build(&ui, || {
                    ui.separator();
                    ui.text("Hello World!");
                });
        }

        let mut encoder = self.device.create_command_encoder(&CommandEncoderDescriptor {
            label: Some("imgui_command_renderer"),
        });

        if self.last_cursor != ui.mouse_cursor() {
            self.last_cursor = ui.mouse_cursor();
            self.platform.prepare_render(&ui, window);
        }

        let view = surface_texture.texture.create_view(&TextureViewDescriptor::default());

        let mut render_pass = encoder.begin_render_pass(&RenderPassDescriptor {
            label: Some("imgui_render_pass"),
            color_attachments: &[Some(RenderPassColorAttachment {
                view: &view,
                resolve_target: None,
                ops: Operations {
                    load: LoadOp::Load,
                    store: true,
                },
            })],
            depth_stencil_attachment: None,
        });

        self.renderer.render(
            ui.render(),
            self.queue.as_ref(),
            self.device.as_ref(),
            &mut render_pass,
        ).expect("Fatal error: Gui rendering failed");

        drop(render_pass);
        
        self.queue.submit(Some(encoder.finish()));
    }
}

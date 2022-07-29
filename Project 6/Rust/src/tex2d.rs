use image::GenericImageView;
use wgpu::util::*;
use wgpu::*;

use std::mem::size_of;
use std::rc::Rc;

use bytemuck::{Pod, Zeroable};

#[repr(C)]
#[derive(Debug, Copy, Clone, Pod, Zeroable)]
pub struct Vertex {
    pub pos: [f32; 2],
    pub tex_coord: [f32; 2],
}

impl Vertex {
    const ATTRIBS: &'static [VertexAttribute] = &vertex_attr_array![0 => Float32x2, 1 => Float32x2];

    fn description() -> VertexBufferLayout<'static> {
        VertexBufferLayout {
            array_stride: size_of::<Vertex>() as BufferAddress,
            step_mode: VertexStepMode::Vertex,
            attributes: Vertex::ATTRIBS,
        }
    }
}

const IMG_INDICES: [u16; 6] = [0, 1, 2, 3, 0, 2];

pub struct Tex2D {
    device: Rc<Device>,
    queue: Rc<Queue>,
    render_pipeline: RenderPipeline,
    bind_group: BindGroup,
    vertices: [Vertex; 4],
    vert_buf: Buffer,
    ind_buf: Buffer,
}

impl Tex2D {
    pub fn new(
        device: &Rc<Device>,
        queue: &Rc<Queue>,
        tex_format: &TextureFormat,
        vertices: [Vertex; 4],
        img_bytes: &[u8],
    ) -> Self {
        let shader = device.create_shader_module(include_wgsl!("../bkgshdr.wgsl"));

        let img = image::load_from_memory(img_bytes).unwrap();
        let img_rgba = img.to_rgba8();

        let dimensions = img.dimensions();

        let texture_size = Extent3d {
            width: dimensions.0,
            height: dimensions.1,
            depth_or_array_layers: 1,
        };
        let img_texture = device.create_texture(&TextureDescriptor {
            size: texture_size,
            mip_level_count: 1,
            sample_count: 1,
            dimension: TextureDimension::D2,
            format: TextureFormat::Rgba8UnormSrgb,
            usage: TextureUsages::TEXTURE_BINDING | TextureUsages::COPY_DST,
            label: Some("img_texture"),
        });

        queue.write_texture(
            ImageCopyTexture {
                texture: &img_texture,
                mip_level: 0,
                origin: Origin3d::ZERO,
                aspect: TextureAspect::All,
            },
            &img_rgba,
            ImageDataLayout {
                offset: 0,
                bytes_per_row: std::num::NonZeroU32::new(4 * dimensions.0),
                rows_per_image: std::num::NonZeroU32::new(dimensions.1),
            },
            texture_size,
        );

        let img_texture_view = img_texture.create_view(&TextureViewDescriptor::default());
        let img_sampler = device.create_sampler(&SamplerDescriptor {
            address_mode_u: AddressMode::ClampToEdge,
            address_mode_v: AddressMode::ClampToEdge,
            address_mode_w: AddressMode::ClampToEdge,
            mag_filter: FilterMode::Linear,
            min_filter: FilterMode::Nearest,
            mipmap_filter: FilterMode::Nearest,
            ..Default::default()
        });

        let bind_group_layout = device.create_bind_group_layout(&BindGroupLayoutDescriptor {
            label: Some("texture_bind_group_layout"),
            entries: &[
                BindGroupLayoutEntry {
                    binding: 0,
                    visibility: ShaderStages::FRAGMENT,
                    ty: BindingType::Texture {
                        sample_type: TextureSampleType::Float { filterable: true },
                        view_dimension: TextureViewDimension::D2,
                        multisampled: false,
                    },
                    count: None,
                },
                BindGroupLayoutEntry {
                    binding: 1,
                    visibility: ShaderStages::FRAGMENT,
                    ty: BindingType::Sampler(SamplerBindingType::Filtering),
                    count: None,
                },
            ],
        });

        let bind_group = device.create_bind_group(&BindGroupDescriptor {
            layout: &bind_group_layout,
            label: Some("map_bind_group"),
            entries: &[
                BindGroupEntry {
                    binding: 0,
                    resource: BindingResource::TextureView(&img_texture_view),
                },
                BindGroupEntry {
                    binding: 1,
                    resource: BindingResource::Sampler(&img_sampler),
                },
            ],
        });

        let render_pipeline_layout = device.create_pipeline_layout(&PipelineLayoutDescriptor {
            label: Some("map_render_pipeline_layout"),
            bind_group_layouts: &[&bind_group_layout],
            push_constant_ranges: &[],
        });

        let render_pipeline = device.create_render_pipeline(&RenderPipelineDescriptor {
            label: Some("map_render_pipeline"),
            layout: Some(&render_pipeline_layout),
            vertex: VertexState {
                module: &shader,
                entry_point: "vs_main",
                buffers: &[Vertex::description()],
            },
            primitive: PrimitiveState {
                topology: PrimitiveTopology::TriangleList,
                strip_index_format: None,
                front_face: FrontFace::Ccw,
                cull_mode: Some(Face::Back),
                unclipped_depth: false,
                polygon_mode: PolygonMode::Fill,
                conservative: false,
            },
            depth_stencil: None,
            multisample: MultisampleState {
                count: 1,
                mask: !0,
                alpha_to_coverage_enabled: false,
            },
            fragment: Some(FragmentState {
                module: &shader,
                entry_point: "fs_main",
                targets: &[Some(ColorTargetState {
                    format: *tex_format,
                    blend: Some(BlendState::ALPHA_BLENDING),
                    write_mask: ColorWrites::ALL,
                })],
            }),
            multiview: None,
        });

        let vert_buf = device.create_buffer_init(&BufferInitDescriptor {
            label: Some("map_vertex_buffer"),
            contents: bytemuck::cast_slice(&vertices),
            usage: BufferUsages::VERTEX | BufferUsages::COPY_DST,
        });

        let ind_buf = device.create_buffer_init(&BufferInitDescriptor {
            label: Some("map_index_buffer"),
            contents: bytemuck::cast_slice(&IMG_INDICES),
            usage: BufferUsages::INDEX,
        });

        Self {
            device: Rc::clone(&device),
            queue: Rc::clone(&queue),
            render_pipeline,
            bind_group,
            vertices,
            vert_buf,
            ind_buf,
        }
    }

    pub fn draw(&self, surface_texture: &SurfaceTexture) {
        let view = surface_texture
            .texture
            .create_view(&TextureViewDescriptor::default());

        let mut encoder = self
            .device
            .create_command_encoder(&CommandEncoderDescriptor {
                label: Some("map_command_encoder"),
            });

        let mut render_pass = encoder.begin_render_pass(&RenderPassDescriptor {
            label: Some("map_render_pass"),
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

        render_pass.set_pipeline(&self.render_pipeline);
        render_pass.set_bind_group(0, &self.bind_group, &[]);
        render_pass.set_vertex_buffer(0, self.vert_buf.slice(..));
        render_pass.set_index_buffer(self.ind_buf.slice(..), IndexFormat::Uint16);
        render_pass.draw_indexed(0..6, 0, 0..1);

        drop(render_pass);

        self.queue.submit(std::iter::once(encoder.finish()));
    }

    /// Return (width, height)
    pub fn get_dimensions(&self) -> (f32, f32) {
        (
            (self.vertices[0].pos[0] - self.vertices[2].pos[0]).abs(),
            (self.vertices[0].pos[1] - self.vertices[2].pos[1]).abs()
        )
    }

    pub fn change_position(&self, new_position: [[f32; 2]; 4]) {
        let stride = size_of::<Vertex>();

        self.queue.write_buffer(
            &self.vert_buf,
            0,
            bytemuck::cast_slice(&new_position[0])
        );

        for (i, vertex_pos) in new_position.iter().enumerate() {
            self.queue.write_buffer(
                &self.vert_buf,
                (stride * i) as BufferAddress,
                bytemuck::cast_slice(vertex_pos)
            )
        }
    }
}

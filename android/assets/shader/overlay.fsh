#ifdef GL_ES
precision highp float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_position;
uniform vec2 u_size;
uniform float u_shape;
uniform float u_time;
uniform float u_alpha;

float fillmask(float shape){
    return clamp(-shape, 0.0, 1.0);
}

float drawcircle(vec2 p, float radius){
    return length(p) - radius;
}

float rectangle(vec2 uv, vec2 pos, float width, float height) {
	float t = 0.0;
	if ((uv.x > pos.x - width / 2.0) && (uv.x < pos.x + width / 2.0)
		&& (uv.y > pos.y - height / 2.0) && (uv.y < pos.y + height / 2.0)) {
		t = 1.0;
	}
	return t;
}

void main() {
    vec2 uv = gl_FragCoord.xy;
	
	float a = 0.0;
	const float timespan = 0.3;
	
	if(u_time <= timespan)
		a = smoothstep(0.0, 1.0, fract(u_time / timespan));
	else
		a = 1.0;
	
	vec4 color = vec4(0, 0, 0, u_alpha * 0.5);
	
	float shape;
	
	if(u_shape == 0.0){
		shape = rectangle(uv, u_position, u_size.x, u_size.y);
		color = mix(color, vec4(0, 0, 1, 0), shape * u_alpha);
	}

	if(u_shape == 1.0){
		shape = drawcircle(uv - u_position, u_size.x * 0.5);
		color = mix(color, vec4(0, 0, 1, 0), fillmask(shape) * u_alpha);
	}
	
	gl_FragColor = color;
}
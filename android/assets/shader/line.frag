#ifdef GL_ES
precision highp float;
#endif

uniform vec2 u_resolution;
uniform vec2 pointA;
uniform vec2 pointB;
uniform float thickness;
uniform float r;
uniform float g;
uniform float b;

void main() {
    vec2 uv = gl_FragCoord.xy;
	vec2 dir = pointA - pointB;
	float lngth = length(dir);
	dir /= lngth;
	vec2 proj = max(0.0, min(lngth, dot((pointA - uv), dir))) * dir;
	float line = length((pointA - uv) - proj) - thickness;

	vec4 color = vec4(0, 0, 0, 0);
	color = mix(color, vec4(r,g,b,1), clamp(-line, 0.0, 1.0));
	gl_FragColor = color;
}
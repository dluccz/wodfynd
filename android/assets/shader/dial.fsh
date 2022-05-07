#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform float u_time;
uniform float u_dial_diameter;
uniform vec2 u_center;
uniform float u_alpha;
uniform vec2 u_pos;

void main() {
    vec2 p = vec2(gl_FragCoord.x-u_resolution.x * u_center.x - u_pos.x, gl_FragCoord.y-u_resolution.y * u_center.y - u_pos.y)/u_dial_diameter;

    float tau = 3.1415926535*2.0;
    float a = atan(p.x,p.y);
    float r = length(p);
    vec2 uv = vec2(a/tau,r);

	float xCol = (uv.x - (u_time / 3.0)) * 3.0;
	xCol = mod(xCol, 3.0);
	vec3 horColour = vec3(0.25, 0.25, 0.25);

	if (xCol < 1.0){
		horColour.r += 1.0 - xCol;
		horColour.g += xCol;
	}else if (xCol < 2.0){
		xCol -= 1.0;
		horColour.g += 1.0 - xCol;
		horColour.b += xCol;
	}else{
		xCol -= 2.0;
		horColour.b += 1.0 - xCol;
		horColour.r += xCol;
	}

	uv = (2.0 * uv) - 1.0;
	float beamWidth = (0.7+0.01*cos(uv.x*10.0*tau*0.15*clamp(floor(5.0 + 10.0*cos(u_time)), 0.0, 10.0))) * abs(1.0 / (30.0 * uv.y));

	vec4 color = vec4(0,0,0,0);
	color = mix(color, vec4(horColour, 1), beamWidth * u_alpha);
	gl_FragColor = color;
}
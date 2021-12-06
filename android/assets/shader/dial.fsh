precision mediump float;

uniform float time;
uniform vec2 resolution;
uniform float diameter;

varying vec2 fragCoord;

const float PI = 3.14159265;

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {


	
	
	vec2 p = vec2(2.0*fragCoord.x-resolution.x, 2.0*fragCoord.y-resolution.y)/400.0;




    //vec2 p = (2.0*fragCoord.xy-resolution.xy)/resolution.y;
    float tau = 3.1415926535*2.0;

    float a = atan(p.x,p.y);
    float r = length(p)*0.75;
    vec2 uv = vec2(a/tau,r);
	
	//get the color
	float xCol = (uv.x - (time / 3.0)) * 3.0;
	xCol = mod(xCol, 3.0);
	vec3 horColour = vec3(0.25, 0.25, 0.25);

	//fragColor = vec4(xCol);
	//return;
	
	if (xCol < 1.0) {
		
		horColour.r += 1.0 - xCol;
		horColour.g += xCol;
	}
	else if (xCol < 2.0) {
		
		xCol -= 1.0;
		horColour.g += 1.0 - xCol;
		horColour.b += xCol;
	}
	else {
		
		xCol -= 2.0;
		horColour.b += 1.0 - xCol;
		horColour.r += xCol;
	}

	// draw color beam
	uv = (2.0 * uv) - 1.0;
	float beamWidth = (0.7+0.5*cos(uv.x*10.0*tau*0.15*clamp(floor(5.0 + 10.0*cos(time)), 0.0, 10.0))) * abs(1.0 / (30.0 * uv.y));

	
	
	vec4 color = vec4(0,0,0,0);
	color = mix(color, vec4(horColour, 1), beamWidth * 1.0);
	fragColor = color;
	//fragColor = vec4(diameter, 0, 0, 1);
}

void main(void)
{
    mainImage(gl_FragColor, fragCoord.xy);
    //gl_FragColor.a = 1.0;
}
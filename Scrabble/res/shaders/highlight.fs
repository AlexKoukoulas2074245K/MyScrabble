uniform sampler2D myTexture;
uniform vec3 darknessFactor;
uniform int highlighted;
uniform int fullWhite;

varying vec2 vTexCoord; 
vec4 standardBrightness = vec4(1.3, 1.3, 1.3, 1);

int brightnessMultiplier = 0;

void main()
{
    gl_FragColor = texture2D(myTexture, vTexCoord).rgba * vec4(darknessFactor, 1);
    
    if(highlighted == 1)
    {
    	brightnessMultiplier ++;	   
    }
    
    if(fullWhite == 1)
    {
    	brightnessMultiplier += 2;
    }
    
    if(brightnessMultiplier > 0)
    {
    	gl_FragColor = gl_FragColor * brightnessMultiplier * standardBrightness;
    }
}
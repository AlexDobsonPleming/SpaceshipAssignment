#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform vec3 viewPos;
uniform sampler2D first_texture;
uniform sampler2D second_texture;

struct Light {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
};

struct Spotlight {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  vec3 direction;
  float cutoff;
  float outerCutoff;
  float constant;
  float linear;
  float quadratic;
};



#define MAX_POINT_LIGHTS 10  
uniform Light lights[MAX_POINT_LIGHTS];
uniform int numLights;

uniform Spotlight spotlight;

struct Material {
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
}; 
  
uniform Material material;

vec3 CalcPointLight(Light light, vec3 norm, vec3 aPos, vec3 viewDir) {
  // ambient
  vec3 ambient = light.ambient * texture(first_texture, aTexCoord).rgb;

  // diffuse
  vec3 lightDir = normalize(light.position - aPos);  
  float diff = max(dot(norm, lightDir), 0.0);
  vec3 diffuse = light.diffuse * diff  * texture(first_texture, aTexCoord).rgb;
  
  // specular 
  vec3 reflectDir = reflect(-lightDir, norm);  
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  vec3 specular = light.specular * spec * vec3(texture(second_texture, aTexCoord));
 
  vec3 result = ambient + diffuse + specular;
  return result;
}

vec3 CalcSpotlight(Spotlight light, vec3 norm, vec3 aPos, vec3 viewDir) {
  // Sample the diffuse texture and perform transparency check
  vec4 texture_colour = texture(first_texture, aTexCoord);
  if (texture_colour.a < 0.1) discard;

  // Calculate light direction and spotlight cutoff
  vec3 lightDir = normalize(light.position - aPos);
  float theta = dot(lightDir, normalize(-light.direction));

  if (theta > light.outerCutoff) { // Within the cone of the spotlight
    // Diffuse component
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * texture_colour.rgb;

    // Specular component
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * spec * vec3(texture(second_texture, aTexCoord));

    // Smooth spotlight edge intensity
    float epsilon = light.outerCutoff - light.cutoff;
    float intensity = clamp((theta - light.cutoff) / epsilon, 0.0, 1.0);

    // Attenuation
    float distance = length(light.position - aPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // Combine components
    return intensity * attenuation * (diffuse + specular);
  } else {
    return vec3(0.0); // Outside the spotlight cone
  }
}


void main() {
  vec3 norm = normalize(aNormal);
  vec3 viewDir = normalize(viewPos - aPos);

  vec3 result = vec3(0.0);
  for (int i = 0; i < numLights; i++)
  result += CalcPointLight(lights[i], norm, aPos, viewDir);

  result += CalcSpotlight(spotlight, norm, aPos, viewDir);

  fragColor = vec4(result, 1.0);
}
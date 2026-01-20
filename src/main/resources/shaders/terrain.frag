#version 330 core

in vec3 vNormal;
in vec3 vWorldPos;

uniform vec3 uLightDir;
uniform vec3 uCameraPos;

out vec4 FragColor;

void main() {
    vec3 normal = normalize(vNormal);
    vec3 lightDir = normalize(-uLightDir);
    float diff = max(dot(normal, lightDir), 0.0);

    vec3 viewDir = normalize(uCameraPos - vWorldPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);

    float height = vWorldPos.y;
    vec3 baseColor = mix(vec3(0.14, 0.3, 0.18), vec3(0.48, 0.42, 0.28), smoothstep(-2.0, 8.0, height));

    vec3 ambient = baseColor * 0.25;
    vec3 diffuse = baseColor * diff;
    vec3 specular = vec3(0.9) * spec * 0.2;

    FragColor = vec4(ambient + diffuse + specular, 1.0);
}

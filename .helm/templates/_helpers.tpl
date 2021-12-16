{{- define "app.roleArn" -}}
{{- if eq .Values.werf.env "prod" -}}
arn:aws:iam::585533447476:role/bl-network-prod-service
{{- else -}}
arn:aws:iam::585533447476:role/BackendServiceRole_{{ .Values.werf.env }}
{{- end -}}
{{- end -}}

{{- define "app.clusterIssuer" -}}
{{- if eq .Values.werf.env "prod" -}}
zerossl-prod
{{- else -}}
cert-manager
{{- end -}}
{{- end -}}

{{- define "app.subname" -}}
{{- if eq .Values.werf.env "prod" -}}
app
{{- else -}}
{{ .Values.werf.env }}
{{- end -}}
{{- end -}}

{{- define "app.hostname" -}}
{{ .Values.werf.name }}.{{ include "app.subname" . }}.briolink.com
{{- end -}}

{{- define "app.envVars" -}}
- name: spring_profiles_active
  value: {{ .Values.werf.env | quote }}
- name: DB_HOST
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbHost
- name: DB_PORT
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbPort
- name: DB_USER
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbUsername
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbPassword
{{- end -}}

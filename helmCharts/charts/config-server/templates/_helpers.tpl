{{/*
Generate a fullname for the config-server
*/}}
{{- define "config-server.fullname" -}}
{{- printf "%s-config-server" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Just return the name of the config-server
*/}}
{{- define "config-server.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create chart name and version as used by the chart label
*/}}
{{- define "config-server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end }}

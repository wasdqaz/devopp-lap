{{/*
Generate a fullname for the admin-server
*/}}
{{- define "admin-server.fullname" -}}
{{- printf "%s-admin-server" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Just return the name of the admin-server
*/}}
{{- define "admin-server.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create chart name and version as used by the chart label
*/}}
{{- define "admin-server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Generate a fullname for the discovery-server
*/}}
{{- define "discovery-server.fullname" -}}
{{- printf "%s-discovery-server" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Just return the name of the discovery-server
*/}}
{{- define "discovery-server.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create chart name and version as used by the chart label
*/}}
{{- define "discovery-server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end }}

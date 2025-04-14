{{/*
Generate a fullname for the visits-service
*/}}
{{- define "visits-service.fullname" -}}
{{- printf "%s-visits-service" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Just return the name of the visits-service
*/}}
{{- define "visits-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create chart name and version as used by the chart label
*/}}
{{- define "visits-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Generate a fullname for the vets-service
*/}}
{{- define "vets-service.fullname" -}}
{{- printf "%s-vets-service" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Just return the name of the vets-service
*/}}
{{- define "vets-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create chart name and version as used by the chart label
*/}}
{{- define "vets-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end }}

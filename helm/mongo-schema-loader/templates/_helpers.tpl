{{- define "mongo-schema-loader.fullname" -}}
{{ printf "%s-%s" .Release.Name .Chart.Name }}
{{- end }}

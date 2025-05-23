output "instance_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.app_server.public_ip
}

output "kms_key_id" {
  description = "KMS key ID"
  value       = aws_kms_key.team_key.key_id
}

output "secret_arn" {
  description = "ARN of the created secret"
  value       = aws_secretsmanager_secret.db_credentials.arn
}

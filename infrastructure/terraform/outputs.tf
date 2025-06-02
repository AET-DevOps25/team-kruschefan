output "instance_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.team_kruschefan_ec2.public_ip
}

output "kms_key_id" {
  description = "KMS key ID"
  value       = aws_kms_key.team_kruschefan_kms.arn
}

output "secret_arn" {
  description = "ARN of the created secret"
  value       = aws_secretsmanager_secret.team_kruschefan_credentials.arn
}

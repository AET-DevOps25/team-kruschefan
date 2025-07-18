output "public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.team_kruschefan_project.public_ip
}

output "ssh_connection" {
  description = "SSH connection string"
  value       = "ubuntu@${aws_instance.team_kruschefan_project.public_ip}"
}

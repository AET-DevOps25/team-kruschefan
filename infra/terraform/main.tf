provider "aws" {
  region = var.aws_region
}

# Define an SSH key pair for connecting to the EC2 instance
resource "aws_key_pair" "team_kruschefan_key" {
  key_name   = var.ssh_key_name # Name visible in AWS Console
  public_key = file(var.public_key_path) # Path to your local public key
}

# Create a security group allowing SSH and HTTP access
resource "aws_security_group" "team_kruschefan_sg" {
  name        = "ec2-ssh-web"
  description = "Allow SSH and HTTP"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Open SSH from anywhere
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Open HTTP from anywhere
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1" # Allow all outbound traffic
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Define the EC2 instance to launch
resource "aws_instance" "team_kruschefan_ec2" {
  ami           = var.ami_id # Amazon Linux 2 AMI in us-east-1
  instance_type = var.instance_type # Free tier
  key_name          = aws_key_pair.team_kruschefan_key.key_name # Use the created SSH key
  security_groups   = [aws_security_group.team_kruschefan_sg.name] # Attach the security group

  tags = {
    Name = "team-kruschefan-ec2" # Tag in the AWS Console
  }

  provisioner "remote-exec" {
    inline = [
      "echo Waiting for SSH...",
      "while ! nc -z localhost 22; do sleep 5; done"
    ]

    connection {
      type        = "ssh"
      user        = var.ami_user
      private_key = file(var.private_key_path)
      host        = self.public_ip
    }
  }

  provisioner "local-exec" {
    command = "ANSIBLE_CONFIG=../ansible/ansible.cfg ansible-playbook -i ${self.public_ip}, -u ${var.ami_user} --private-key ${var.private_key_path} ../ansible/playbook.yml"

    connection {
      type        = "ssh"
      user        = var.ami_user
      private_key = file(var.private_key_path)
      host        = self.public_ip
    }
  }
}

# KMS
resource "aws_kms_key" "team_kruschefan_kms" {
  description         = "KMS key for encrypting secrets"
  deletion_window_in_days = 10
  enable_key_rotation = true
}

# Secrets
resource "aws_secretsmanager_secret" "team_kruschefan_credentials" {
  name        = "team/credentials"
  description = "Database username and password"
  kms_key_id  = aws_kms_key.team_kruschefan_kms.arn
}

resource "aws_secretsmanager_secret_version" "team_kruschefan_credentials_version" {
  secret_id     = aws_secretsmanager_secret.team_kruschefan_credentials.id
  secret_string = var.secret_string
}

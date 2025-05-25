provider "aws" {
  region = "us-east-1" # Change to your preferred AWS region
}

# Define an SSH key pair for connecting to the EC2 instance
resource "aws_key_pair" "team_kruschefan_key" {
  key_name   = "team-key" # Name visible in AWS Console
  public_key = file("~/.ssh/team-key.pub") # Path to your local public key
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
  ami           = "ami-0c02fb55956c7d316" # Amazon Linux 2 AMI in us-east-1
  instance_type = "t2.micro" # Free tier
  key_name          = aws_key_pair.team_kruschefan_key.key_name # Use the created SSH key
  security_groups   = [aws_security_group.team_kruschefan_sg.name] # Attach the security group

  tags = {
    Name = "team-kruschefan-ec2" # Tag in the AWS Console
  }

  # Upload local start.sh
  provisioner "file" {
    source      = "${path.module}/start.sh"
    destination = "/home/ec2-user/start.sh"
  }

  # Remote setup and run start.sh
  provisioner "remote-exec" {
    inline = [
      "sudo yum update -y",
      "sudo yum install -y jq git",

      # Docker
      "sudo amazon-linux-extras install docker -y",
      "sudo service docker start",
      "sudo usermod -a -G docker ec2-user",

      # Helm
      "curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash",

      # kubectl
      "curl -LO https://dl.k8s.io/release/$(curl -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl",
      "chmod +x kubectl",
      "sudo mv kubectl /usr/local/bin",

      # Execute app startup script
      "chmod +x /home/ec2-user/start.sh",
      "/home/ec2-user/start.sh"
    ]


    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/team-key") # Local path to your private key
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
  secret_id     = aws_secretsmanager_secret.app.id
  secret_string = var.secret_string
}

provider "aws" {
  region = var.aws_region
}

resource "aws_key_pair" "team_kruschefan_key" {
  key_name   = var.ssh_key_name
  public_key = file(var.public_key_path)
}

resource "aws_security_group" "team_kruschefan_sg" {
  name        = "ec2-ssh-web"
  description = "Allow SSH and HTTP"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "team_kruschefan_ec2" {
  ami           = var.ami_id
  instance_type = var.instance_type
  key_name      = aws_key_pair.team_kruschefan_key.key_name

  vpc_security_group_ids = [aws_security_group.team_kruschefan_sg.id]

  tags = {
    Name = "team-kruschefan-ec2"
  }

  provisioner "local-exec" {
    command = "echo 'EC2 instance ${self.public_ip} created.'"
  }

  lifecycle {
    ignore_changes = [user_data]
  }
}

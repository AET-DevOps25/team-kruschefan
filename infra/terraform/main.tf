provider "aws" {
  region = var.aws_region
}

# SSM 
resource "aws_ssm_parameter" "env_vars" {
  for_each = var.env_vars

  name  = "/team-kruschefan-project/${each.key}"
  type  = "SecureString"
  value = each.value
  overwrite = true
}

resource "aws_iam_role" "ec2_ssm_role" {
  name = "ec2-ssm-access"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "attach_ssm_policy" {
  role       = aws_iam_role.ec2_ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess"
}

resource "aws_iam_instance_profile" "ec2_ssm_profile" {
  name = "ec2-ssm-profile"
  role = aws_iam_role.ec2_ssm_role.name
}

# EC2
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

resource "aws_instance" "team_kruschefan_project" {
  ami           = var.ami_id
  instance_type = var.instance_type
  key_name      = aws_key_pair.team_kruschefan_key.key_name

  vpc_security_group_ids = [aws_security_group.team_kruschefan_sg.id]

  tags = {
    Name = "team_kruschefan_project"
  }

  provisioner "local-exec" {
    command = "echo 'EC2 instance ${self.public_ip} created.'"
  }

  lifecycle {
    ignore_changes = [user_data]
  }

  iam_instance_profile = aws_iam_instance_profile.ec2_ssm_profile.name

  user_data = <<-EOF
              #!/bin/bash
              VAR_KEYS=$(aws ssm get-parameters-by-path --path "/team-kruschefan-project" --with-decryption --query "Parameters[*].[Name,Value]" --output text)

              echo "$VAR_KEYS" | while read name value; do
                key=$(basename "$name")
                echo "$key=$value" >> /etc/environment
              done
              EOF
}

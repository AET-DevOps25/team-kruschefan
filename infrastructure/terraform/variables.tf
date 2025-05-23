# AWS region to deploy to
variable "aws_region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "us-east-1"
}

# SSH key paths
variable "ssh_key_name" {
  description = "Name of the key pair to use for SSH"
  type        = string
  default     = "team-key"
}

variable "public_key_path" {
  description = "Path to the local public SSH key"
  type        = string
  default     = "~/.ssh/team-key.pub"
}

variable "private_key_path" {
  description = "Path to the local private SSH key"
  type        = string
  default     = "~/.ssh/team-key"
}

# EC2 settings
variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t2.micro"
}

variable "ami_id" {
  description = "AMI ID to use for EC2 instance"
  type        = string
  default     = "ami-0c02fb55956c7d316" # Amazon Linux 2 in us-east-1
}

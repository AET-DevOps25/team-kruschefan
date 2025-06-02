# AWS region to deploy to
variable "aws_region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "eu-central-1"
}

# SSH key paths
variable "ssh_key_name" {
  description = "Name of the key pair to use for SSH"
  type        = string
  default     = "devops"
}

variable "public_key_path" {
  description = "Path to the local public SSH key"
  type        = string
  default     = "~/.ssh/devops.pub"
}

variable "private_key_path" {
  description = "Path to the local private SSH key"
  type        = string
  default     = "~/.ssh/devops"
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
  default     = "ami-0a87a69d69fa289be" # for eu-central-1
}

variable "ami_user" {
  description = "default user for ssh connection"
  type        = string
  default     = "ubuntu"
}

variable "secret_string" {
  description = "JSON string with database credentials"
  type        = string
}
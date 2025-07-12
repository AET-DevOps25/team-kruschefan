# AWS settings
variable "aws_region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "eu-central-1"
}

# EC2 instance configuration
variable "ami_id" {
  description = "AMI ID for EC2 instance"
  type        = string
  default     = "ami-0a87a69d69fa289be" # update as needed
}

variable "instance_type" {
  description = "Instance type for EC2"
  type        = string
  default     = "t2.micro"
}

# SSH settings
variable "ssh_key_name" {
  description = "Name of the SSH key pair in AWS"
  type        = string
  default     = "devops"
}

variable "public_key_path" {
  description = "Path to local public SSH key"
  type        = string
  default     = "~/.ssh/devops.pub"
}

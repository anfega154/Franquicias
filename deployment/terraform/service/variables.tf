variable "aws_region" {
  type        = string
  description = "AWS region for application resources."
  default     = "us-east-1"
}

variable "project" {
  type        = string
  description = "Project name prefix used to name AWS resources."
  default     = "franquicias"
}

variable "environment" {
  type        = string
  description = "Environment name."
  default     = "dev"
}

variable "app_port" {
  type        = number
  description = "Container and target group port."
  default     = 8080
}

variable "desired_count" {
  type        = number
  description = "Desired ECS tasks."
  default     = 1
}

variable "container_cpu" {
  type        = number
  description = "CPU units for the ECS task."
  default     = 512
}

variable "container_memory" {
  type        = number
  description = "Memory in MiB for the ECS task."
  default     = 1024
}

variable "health_check_path" {
  type        = string
  description = "ALB health check path."
  default     = "/actuator/health"
}

variable "api_gateway_stage_name" {
  type        = string
  description = "Stage name for the public HTTP API Gateway."
  default     = "$default"
}

variable "mongodb_secret_name" {
  type        = string
  description = "Name of the AWS Secrets Manager secret that stores MONGO_URI."
}

variable "image_tag" {
  type        = string
  description = "Container image tag deployed to ECS."
  default     = "latest"
}

variable "vpc_cidr" {
  type        = string
  description = "CIDR block for the VPC."
  default     = "10.20.0.0/16"
}

variable "public_subnet_cidrs" {
  type        = list(string)
  description = "CIDRs for public subnets."
  default     = ["10.20.1.0/24", "10.20.2.0/24"]
}

variable "private_subnet_cidrs" {
  type        = list(string)
  description = "CIDRs for private subnets."
  default     = ["10.20.11.0/24", "10.20.12.0/24"]
}

variable "log_retention_days" {
  type        = number
  description = "CloudWatch log retention in days."
  default     = 14
}

variable "tags" {
  type        = map(string)
  description = "Extra tags merged into all resources."
  default     = {}
}

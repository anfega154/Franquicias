variable "aws_region" {
  type        = string
  description = "AWS region for Terraform bootstrap resources."
  default     = "us-east-2"
}

variable "terraform_state_bucket" {
  type        = string
  description = "Globally unique S3 bucket name used for Terraform remote state."
}

variable "terraform_lock_table" {
  type        = string
  description = "DynamoDB table name used for Terraform state locking."
}

variable "tags" {
  type        = map(string)
  description = "Tags applied to bootstrap resources."
  default = {
    project     = "franquicias"
    managed-by  = "terraform"
    environment = "shared"
  }
}

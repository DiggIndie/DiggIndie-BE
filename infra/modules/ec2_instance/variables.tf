# modules/ec2_instance/variables.tf

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
}

variable "environment" {
  description = "환경"
  type        = string
}

variable "instance_type" {
  description = "EC2 인스턴스 타입"
  type        = string
  default     = "t2.nano"
}

variable "key_name" {
  description = "SSH 키 페어 이름"
  type        = string
}

variable "subnet_id" {
  description = "서브넷 ID"
  type        = string
}

variable "security_group_id" {
  description = "보안 그룹 ID"
  type        = string
}

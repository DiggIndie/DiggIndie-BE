# variables.tf - 전역 변수 정의

variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "spring-vote"
}

variable "environment" {
  description = "환경 (dev, staging, prod)"
  type        = string
  default     = "dev"
}

# VPC 관련
variable "vpc_cidr" {
  description = "VPC CIDR 블록"
  type        = string
  default     = "10.0.0.0/16"
}

# EC2 관련
variable "instance_type" {
  description = "EC2 인스턴스 타입"
  type        = string
  default     = "t2.nano"
}

variable "key_name" {
  description = "SSH 키 페어 이름"
  type        = string
}

# RDS 관련
variable "db_name" {
  description = "데이터베이스 이름"
  type        = string
  default     = "springvote"
}

variable "db_username" {
  description = "데이터베이스 사용자명"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "데이터베이스 비밀번호"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "RDS 인스턴스 클래스"
  type        = string
  default     = "db.t3.micro"
}

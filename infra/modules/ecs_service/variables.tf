# modules/ecs_service/variables.tf

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
}

variable "environment" {
  description = "환경"
  type        = string
}

variable "cluster_id" {
  description = "ECS 클러스터 ID"
  type        = string
}

variable "subnet_ids" {
  description = "서브넷 ID 목록"
  type        = list(string)
}

variable "security_group_id" {
  description = "보안 그룹 ID"
  type        = string
}

variable "container_image" {
  description = "컨테이너 이미지"
  type        = string
  default     = "nginx:latest"  # TODO: 실제 이미지로 변경
}

variable "container_port" {
  description = "컨테이너 포트"
  type        = number
  default     = 8080
}

variable "task_cpu" {
  description = "Task CPU"
  type        = string
  default     = "256"
}

variable "task_memory" {
  description = "Task 메모리"
  type        = string
  default     = "512"
}

variable "desired_count" {
  description = "원하는 태스크 수"
  type        = number
  default     = 1
}

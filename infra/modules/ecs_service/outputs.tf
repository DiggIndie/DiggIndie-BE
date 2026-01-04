# modules/ecs_service/outputs.tf

output "service_id" {
  description = "ECS 서비스 ID"
  value       = aws_ecs_service.main.id
}

output "service_name" {
  description = "ECS 서비스 이름"
  value       = aws_ecs_service.main.name
}

output "task_definition_arn" {
  description = "Task Definition ARN"
  value       = aws_ecs_task_definition.main.arn
}

output "log_group_name" {
  description = "CloudWatch 로그 그룹 이름"
  value       = aws_cloudwatch_log_group.main.name
}

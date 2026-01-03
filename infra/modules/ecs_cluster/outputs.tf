# modules/ecs_cluster/outputs.tf

output "cluster_id" {
  description = "ECS 클러스터 ID"
  value       = aws_ecs_cluster.main.id
}

output "cluster_arn" {
  description = "ECS 클러스터 ARN"
  value       = aws_ecs_cluster.main.arn
}

output "cluster_name" {
  description = "ECS 클러스터 이름"
  value       = aws_ecs_cluster.main.name
}

# modules/vpc/outputs.tf

output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "퍼블릭 서브넷 ID 목록"
  value       = aws_subnet.public[*].id
}

output "web_security_group_id" {
  description = "웹 서버 보안 그룹 ID"
  value       = aws_security_group.web.id
}

output "db_security_group_id" {
  description = "DB 보안 그룹 ID"
  value       = aws_security_group.db.id
}

output "internet_gateway_id" {
  description = "인터넷 게이트웨이 ID"
  value       = aws_internet_gateway.main.id
}

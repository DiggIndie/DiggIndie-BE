# modules/ec2_instance/outputs.tf

output "instance_id" {
  description = "EC2 인스턴스 ID"
  value       = aws_instance.main.id
}

output "public_ip" {
  description = "퍼블릭 IP"
  value       = aws_eip.main.public_ip
}

output "private_ip" {
  description = "프라이빗 IP"
  value       = aws_instance.main.private_ip
}

output "public_dns" {
  description = "퍼블릭 DNS"
  value       = aws_eip.main.public_dns
}

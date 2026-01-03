# modules/rds/main.tf - PostgreSQL RDS 설정

# DB 서브넷 그룹
resource "aws_db_subnet_group" "main" {
  name        = "${var.project_name}-${var.environment}-db-subnet-group"
  description = "Database subnet group"
  subnet_ids  = var.public_subnet_ids

  tags = {
    Name = "${var.project_name}-${var.environment}-db-subnet-group"
  }
}

# RDS PostgreSQL 인스턴스
resource "aws_db_instance" "main" {
  identifier = "${var.project_name}-${var.environment}-db"

  # 엔진 설정
  engine               = "postgres"
  engine_version       = "15.12"
  instance_class       = var.db_instance_class
  allocated_storage    = 20
  max_allocated_storage = 100
  storage_type         = "gp3"
  storage_encrypted    = true

  # 데이터베이스 설정
  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 5432

  # 네트워크 설정
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.db_security_group_id]

  apply_immediately   = true
  publicly_accessible    = true
  multi_az               = false  # 비용 절감을 위해 단일 AZ

  # 백업 설정
  backup_retention_period = 1
  backup_window          = "03:00-04:00"
  maintenance_window     = "Mon:04:00-Mon:05:00"

  # 기타 설정
  skip_final_snapshot       = true  # 개발환경용
  final_snapshot_identifier = "${var.project_name}-${var.environment}-final-snapshot"
  deletion_protection       = false  # 개발환경용
  auto_minor_version_upgrade = true

  # 파라미터 그룹
  parameter_group_name = aws_db_parameter_group.main.name

  tags = {
    Name = "${var.project_name}-${var.environment}-db"
  }
}

# DB 파라미터 그룹
resource "aws_db_parameter_group" "main" {
  name   = "${var.project_name}-${var.environment}-pg15"
  family = "postgres15"

  parameter {
    name  = "log_connections"
    value = "1"
  }

  parameter {
    name  = "log_disconnections"
    value = "1"
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-pg15"
  }
}

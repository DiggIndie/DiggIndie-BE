<<<<<<< HEAD
# green/main.tf - Green 환경 배포 설정
=======
# green/main.tf - Green EC2 환경
>>>>>>> dev

terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "ap-northeast-2"

  default_tags {
    tags = {
      Project     = "spring-vote"
      Environment = "green"
      ManagedBy   = "Terraform"
    }
  }
}

<<<<<<< HEAD
# 기존 인프라 참조 (Remote State 또는 Data Source 사용)
=======
# 기존 인프라 참조
>>>>>>> dev
data "aws_vpc" "main" {
  tags = {
    Name = "spring-vote-dev-vpc"
  }
}

<<<<<<< HEAD
data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.main.id]
  }

  filter {
    name   = "tag:Type"
    values = ["Public"]
=======
data "aws_subnet" "public" {
  vpc_id = data.aws_vpc.main.id

  filter {
    name   = "tag:Name"
    values = ["spring-vote-dev-public-1"]
>>>>>>> dev
  }
}

data "aws_security_group" "web" {
  vpc_id = data.aws_vpc.main.id

  filter {
    name   = "group-name"
    values = ["spring-vote-dev-web-sg"]
  }
}

<<<<<<< HEAD
data "aws_ecs_cluster" "main" {
  cluster_name = "spring-vote-dev-cluster"
}

# Green ECS Service
module "ecs_service_green" {
  source = "../modules/ecs_service"

  project_name      = "spring-vote"
  environment       = "green"
  cluster_id        = data.aws_ecs_cluster.main.id
  subnet_ids        = data.aws_subnets.public.ids
  security_group_id = data.aws_security_group.web.id
  container_image   = "your-ecr-repo:green"  # TODO: 실제 이미지로 변경
  container_port    = 8080
  desired_count     = 1
}

output "green_service_name" {
  value = module.ecs_service_green.service_name
=======
# Amazon Linux 2023 AMI
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# Green EC2 인스턴스
resource "aws_instance" "green" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = "t2.nano"
  key_name               = "terraform"
  subnet_id              = data.aws_subnet.public.id
  vpc_security_group_ids = [data.aws_security_group.web.id]

  associate_public_ip_address = true

  root_block_device {
    volume_type           = "gp3"
    volume_size           = 30
    delete_on_termination = true
    encrypted             = true
  }

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              yum install -y docker
              systemctl start docker
              systemctl enable docker
              usermod -a -G docker ec2-user
              EOF

  tags = {
    Name = "spring-vote-green"
  }
}

output "green_instance_id" {
  value = aws_instance.green.id
}

output "green_public_ip" {
  value = aws_instance.green.public_ip
>>>>>>> dev
}

provider "aws" {
  region = "eu-west-3"
  access_key = ""
  secret_key = ""
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnet_ids" "default" {
  vpc_id = data.aws_vpc.default.id
}

resource "aws_security_group" "instance" {
  name = "recipes-api-security-group-tf"

  ingress {
    from_port = 9000
    to_port = 9000
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port = 9000
    to_port = 9000
    protocol = "tcp"
    ipv6_cidr_blocks = ["::/0"]
  }
  egress {
      from_port = 0
      to_port = 0
      protocol = "-1"
      cidr_blocks = ["0.0.0.0/0"]
  }
}	

resource "aws_launch_configuration" "recipes_api_lc" {
  image_id = "ami-09bd39708f2dac10a"
  instance_type = "t2.micro"
  security_groups = [aws_security_group.instance.id]

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_autoscaling_group" "recipes_api_asg" {
  launch_configuration = aws_launch_configuration.recipes_api_lc.name
  vpc_zone_identifier = data.aws_subnet_ids.default.ids

  target_group_arns = [aws_lb_target_group.recipes_api_tg.arn]
  health_check_type = "ELB"

  min_size = 2
  max_size = 4

  tag {
    key = "Name"
    value = "recipes-api-asg"
    propagate_at_launch = true
  }
}

resource "aws_lb" "recipes_api_lb" {
  name = "recipes-api"
  load_balancer_type = "application"
  subnets = data.aws_subnet_ids.default.ids
  security_groups = [aws_security_group.recipes_api_alb.id]
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.recipes_api_lb.arn
  port = 9000
  protocol = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Page Not Found"
      status_code = 404
    }
  }
}

resource "aws_security_group" "recipes_api_alb" {
  name = "recipes-api-alb"
  
  ingress {
    from_port = 9000
    to_port = 9000
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
      from_port = 0
      to_port = 0
      protocol = "-1"
      cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_lb_target_group" "recipes_api_tg" {
  name = "recipes-api-tg"
  port = 9000
  protocol = "HTTP"
  vpc_id = data.aws_vpc.default.id

  health_check {
    path = "/"
    protocol = "HTTP"
    matcher = "200"
    interval = 15
    timeout = 5
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener_rule" "asg" {
  listener_arn = aws_lb_listener.http.arn
  priority = 100

  condition {
    path_pattern {
      values = ["*"]
    }
  }

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.recipes_api_tg.arn
  }
}

output "recipes_api_alb_dns_name" {
  value = aws_lb.recipes_api_lb
  description = "Domain name of the Recipes API load balancer"
}
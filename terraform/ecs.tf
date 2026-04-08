resource "aws_ecs_cluster" "dodo_cluster" {
  name = "dodo-cluster"
}

resource "aws_ecs_task_definition" "dodo_task" {
  family                   = "dodo-app"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([{
    name  = "dodo-app"
    image = "grotawred/dodo-app:latest"
    portMappings = [{
      containerPort = 80
      hostPort      = 80
    }]
    environment = [
      { name = "SERVER_PORT", value = "80"},
      { name = "SPRING_DATASOURCE_URL", value = "jdbc:mysql://${aws_db_instance.dodo_db.endpoint}/todoshka2" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = "root1234" }
    ]
  }])
}

resource "aws_ecs_service" "dodo_service" {
  name            = "dodo-service"
  cluster         = aws_ecs_cluster.dodo_cluster.id
  task_definition = aws_ecs_task_definition.dodo_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    assign_public_ip = true
  }
}
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}
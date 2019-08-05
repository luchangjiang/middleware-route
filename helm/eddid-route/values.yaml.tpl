# Default values for eddid-route.
# This is a YAML-formatted file.
# Declare variables to be passed into your templates.
env:
  APPLICATION_PORT: $APPLICATION_PORT
  EUREKA_PORT: $EUREKA_PORT
  EUREKA_HOSTNAME: $EUREKA_HOSTNAME
  EUREKA_USER: $EUREKA_USER
  
secret:
  EUREKA_PASSWORD: $EUREKA_PASSWORD

log:
  elk:
    enabled: true

# Custom label for CI/CD and log use
custom:
  deployment:
    environment: $ENVIRONMENT
    hostname: $APP_HOSTNAME
    strategyType: $K8_DEPLOYMENT_STRATEGY # RollingUpdate / Recreate
  lable:
    scope: $SCOPE_NAME
    service: $SERVICE_NAME

istio:
  sidecar:
    inject: true
  gateway:
    name: $K8_GW_NAME
    
replicaCount: 1

image:
  repository: 'replaced_by_CICD'
  tag: 'replaced_by_CICD'
  pullPolicy: IfNotPresent
  imagePullSecrets: $K8S_REPO_PULL_SECRET

nameOverride: $APP_NAME
fullnameOverride: $STACK_NAME

service:
  type: ClusterIP
  port: 80

ingress:
  enabled: false
  annotations: {}
    # kubernetes.io/ingress.class: nginx
    # kubernetes.io/tls-acme: "true"
  hosts:
    - host: $APP_NAME.local
      paths: []

  tls: []
  #  - secretName: chart-example-tls
  #    hosts:
  #      - chart-example.local

resources: {}
  # We usually recommend not to specify default resources and to leave this as a conscious
  # choice for the user. This also increases chances charts run on environments with little
  # resources, such as Minikube. If you do want to specify resources, uncomment the following
  # lines, adjust them as necessary, and remove the curly braces after 'resources:'.
  # limits:
  #   cpu: 100m
  #   memory: 128Mi
  # requests:
  #   cpu: 100m
  #   memory: 128Mi

nodeSelector: {}

tolerations: []

affinity: {}

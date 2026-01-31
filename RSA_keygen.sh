mkdir ./certs
openssl genrsa -out ./certs/private.pem 4096
openssl rsa -in ./certs/private.pem -pubout -outform PEM -out ./certs/public.pem


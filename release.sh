mvn clean package
mkdir -p artifacts
cp -r target/deliverapp-soap-services-*.jar artifacts/
git add artifacts/*

echo ""
echo "don't forget to change the readme!"

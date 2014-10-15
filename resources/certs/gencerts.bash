rm -rf client
mkdir client
cd client

CNS=(jsnow dtargaryen pbaelish hhodor tlannister astark)
NAMES=("Jon Snow" "Daenerys Targaryen" "Petyr Baelish" "Hodor Hodor" "Tyrion Lannister" "Arya Stark")

element_count=${#CNS[@]}
index=0
while [ "$index" -lt "$element_count" ]
do    # List all the elements in the array.
  echo ${CNS[$index]}
  echo ${NAMES[$index]}

  mkdir ${CNS[$index]}
  cd ${CNS[$index]}

  openssl req -new -newkey rsa:1024 -nodes -out ${CNS[$index]}.req -keyout ${CNS[$index]}.key -subj "/C=US/ST=Virginia/CN=${CNS[$index]}/EMAILADDRESS=aicig-${CNS[$index]}@gmail.com" -days 36500
  openssl x509 -CA ../../ca.pem -CAkey ../../ca.key -CAserial ../../ca.srl -req -in ${CNS[$index]}.req -out ${CNS[$index]}.pem -days 36500
  openssl pkcs12 -export -in ${CNS[$index]}.pem -inkey ${CNS[$index]}.key -out ${CNS[$index]}.p12 -name "${NAMES[$index]}"

  cd ..

  let "index = $index + 1"
done

#openssl req -new -newkey rsa:1024 -nodes -out jsnow.req -keyout jsnow.key -subj '/C=US/ST=Virginia/CN=jsnow/EMAILADDRESS=aicig-jsnow@gmail.com' -days 36500
#openssl x509 -CA ../ca.pem -CAkey ../ca.key -CAserial ../ca.srl -req -in jsnow.req -out jsnow.pem -days 36500
#openssl pkcs12 -export -in jsnow.pem -inkey jsnow.key -out jsnow.p12 -name "Jon Snow"
echo "----------Making CA private key---------"
echo "this will ask for a passphrase, this is just a password for the key it self, write this in row A in the notes"
echo "it will also ask for the following info please check docs for what to use provided is what should be used this will make ca-private.pem"
echo -e "<Feild>,<use>\nCountry Name,NZ\nState or Province Name,Waikato\nLocality Name,Hamilton\nOrganistation,Make up a organisation name\nOrganisation Unit Name,(empty)\nCommon Name,Your Name\nEmail Address,any email" | column -t -s','
read -p "press any key to continue..."
openssl req -new -x509 -keyout ca-private.pem -out ca-cert.pem -days 3650
echo "done"

echo "----------Putting Private key into Java KeyStore----------"
echo "this will ask for a password to protect it, make one then record in row B"
echo "it will also ask if you trust the key, type 'yes'"
read -p "press any key to continue..."
keytool -import -trustcacerts -alias root -file ca-cert.pem -keystore ca-cert.jks
echo "done"

echo "----------Making Servers key pair----------"
echo "for this we need an alias of for the public key, this must be the name of your current machine"
echo "that be a host name or domain registration, this will make a server.jks file which is already stored in java keystore"
echo "this will also prompt for a password to protect the key, make up a password and store in row D"
echo "finally it will prompt the following infomation once again read docs or use what i provide"
echo -e "<Feild>,<use>\nfirst and last name,this is the common name that the key is being signed for will show up later\norganisation unit,(empty)\nOrganistaion,Universisty of Waikato\nCity or Locality,Hamilton\nState or Province,Waikato\nContry Code,NZ" | column -t -s','
echo "then enter 'yes' if all info you have entered matches"
read -p "Enter Alias for public key: " alias
echo "alias entered: " $alias
keytool -genkeypair -alias $alias -keyalg RSA -keystore server.jks
echo "done"

echo "----------Making a Certificate Signing Request (CSR)----------"
echo "this will make server.csr to then be signed by the CA made earlier"
read -p "press any key to continue..."
keytool -certreq -alias $alias -file server.csr -keystore server.jks
echo "done"

echo "----------Signing CSR with CA----------"
echo "this will make server-cert.pem a signed certificate for the public key made by the server"
echo "you will be prompted for the passphrase for the CA private key made earlier (row A)"
read -p "press any key to continue..."
openssl x509 -req -in server.csr -CA ca-cert.pem -CAkey ca-private.pem -CAcreateserial -out server-cert.pem -days 90
echo "done"

echo "----------Putting Certificate and CA's public key into keystore----------"
echo "this is to serve the java applications the keys and certs needed"
read -p "press any key to continue..."
keytool -import -trustcacerts -alias root -file ca-cert.pem -keystore server.jks
echo "stored ca-cert.pem"
keytool -import -alias $alias -file server-cert.pem -keystore server.jks
echo "stored server-cert.pem"
echo "done"
echo

echo "--We now have 3 usefull files--"
echo "ca-private.pem: the private key of the CA used to sign certificates"
echo "ca-cert.jks: the keystore containing the public cert of the CA to be given to the client to make the trust chain"
echo "server.jks: the keystore containing the public certificate of the server, the sevrers private and public keys, and the CA's public key certificate"
echo "--------------------"
echo "this completes the set up process with the named files above, these can be used in the java applications to set up a ssl/tls socket server"
read -p "press any key to finish..."
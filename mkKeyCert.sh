echo "----------Making CA private key---------"
echo "this will ask for a passphrase, this is just a password for the key it self, write this in row A in the notes"
echo "it will also ask for the following info please check docs for what to use provided is what should be used this will make ca-private.pem"
echo -e "<Feild>,<use>\nCountry Name,NZ\nState or Province Name,Waikato\nLocality Name,Hamilton\nOrganistation,Make up a organisation name\nOrganisation Unit Name,(empty)\nCommon Name,Your Name\nEmail Address,any email" | column -t -s','
read -p "press any key to continue..."
#openssl req -new -x509 -keyout ca-private.pem -out ca-cert.pem -days 3650
echo "done"

echo "----------Putting Private key into Java KeyStore----------"
echo "this will ask for a password to protect it, make one then record in row B"
echo "it will also ask if you trust the key, type 'yes'"
read -p "press any key to continue..."
#keytool -import -trustcacerts -alias root -file ca-cert.pem -keystore ca-cert.jks
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
#keytool -genkeypair -alias $alias -keyalg RSA -keystore server.jks
echo "done"

echo ""
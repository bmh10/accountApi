# Create account
curl -XPOST -H "Content-Type: application/json" -d '
 {
   "accountHolderName" : "Account holder 1",
   "currency" : "GBP",
   "balance" : 12.34
 }' localhost:2223/api/v1/account

# View account
curl localhost:2223/api/v1/account/1

# View all accounts
curl localhost:2223/api/v1/account

# Transfer money
curl -XPUT -H "Content-Type: application/json" -d '
 {
   "sourceAccountId" : 1,
   "destinationAccountId" : 2,
   "currency" : "GBP",
   "transferAmount" : 12.34
 }' localhost:2223/api/v1/account

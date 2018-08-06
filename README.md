# evraz-wallet

1. git clone --recursive https://bitbucket.org/zarincheg/evraz-wallet
2.If submodule not init
 git submodule add https://github.com/bituniverse/graphenej.git graphenej
3. Create google-services.json
    3.1 Go to https://developers.google.com/mobile/add?platform=android&cntapi=signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fsign-in%3Fconfigured%3Dtrue&cntlbl=Continue%20Adding%20Sign-In
    3.2 Configure
        App name: Evraz wallet
        Android package name: com.bitshares.bitshareswallet
    3.3* Select analytics tab and select default or new project
    3.4 Download file and put in app/
4. Build and run


* For use google signIn in prod, we create another google-services.json file in owner account with actual Android Signing Certificate SHA-1# evraz-wallet

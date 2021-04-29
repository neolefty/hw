import React, {useCallback, useEffect, useState} from 'react'
import firebase from "firebase/app"
import "firebase/auth"

let app: any = firebase.apps[0]

if (!app) app = firebase.initializeApp({
    apiKey: "AIzaSyA48zA_mjGaqACLP1tllwrh0KuWO4PSHbM",
    authDomain: "tutorial-fc43a.firebaseapp.com",
    projectId: "tutorial-fc43a",
    storageBucket: "tutorial-fc43a.appspot.com",
    messagingSenderId: "512940688147",
    appId: "1:512940688147:web:c7d7d611c78ba5deb450e6",
    measurementId: "G-56GNXMR1SD"
})

export const App = () => {
    const [emailInput, setEmail] = useState<string>('')
    const [error, setError] = useState<Error>()
    const [user, setUser] = useState<firebase.User | null>(null)
    const [message, setMessage] = useState<string>()
    const auth = useProviderAuth(setMessage)
    const url = window.location.toString()
    const isSignin = firebase.auth().isSignInWithEmailLink(url)

    const handleSignIn = useCallback((email: string) => {
        try {
            auth.signin(email, url)
            localStorage.removeItem('emailSent')
        }
        catch(e) {
            setError(e)
        }
    }, [auth, url])

    useEffect(() => {
        if (isSignin && !emailInput) {
            const storedEmail = localStorage.getItem('emailSent')
            if (typeof storedEmail === 'string')
                handleSignIn(storedEmail)
        }
    }, [emailInput, handleSignIn, isSignin])

    useEffect(() => {
        firebase.auth().onAuthStateChanged(setUser)
    }, [])

    return (
        <>
            <h2>Firebase POC: {app.name}</h2>
            {user &&
                <div>
                    Signed in as {user.displayName || user.email || user.phoneNumber}
                    {user?.photoURL && <img src={user.photoURL} alt={user.displayName || 'photo'}/>}
                    &nbsp;—&nbsp;
                    <button onClick={() => firebase.auth().signOut().then(() => setMessage('Signed out'))}>Sign Out</button>
                </div>
            }
            {!user && <div>
                Sign in via email: <input value={emailInput} onChange={e => setEmail(e.target.value)}/>
                &nbsp;—&nbsp;
                {!isSignin && <button onClick={() => emailInput && auth.requestEmail(emailInput)} disabled={!emailInput}>Send Link</button>}
                {isSignin && <button onClick={() => handleSignIn(emailInput)} disabled={!emailInput}>Sign In</button>}
            </div>}
            {message && <div style={{background: '#cfc', padding: '1em'}}>{message}</div>}
            {error && <div style={{background: '#fcc', padding: '1em'}}>{error.message}</div>}
        </>
    )
}

interface ProviderAuth {
    requestEmail: (email: string) => void
    signin: (email: string, link: string) => void
}

const useProviderAuth = (onMessage: (message: string) => void): ProviderAuth => {
    const signin = (email: string, link: string) => {
        if (!firebase.auth().isSignInWithEmailLink(link))
            throw new Error(`Not a signing link: ${link}`)
        firebase.auth().signInWithEmailLink(email, link).then(
            // TODO find the tutorial this came from
            // TODO consider using react-firebase-hooks — https://www.npmtrends.com/react-firebase-hooks-vs-reactfire
            response => onMessage(`Signed in as "${response.user?.email}".`)
        )
    }

    // const requestPhone = (phone: string) => {
    //     firebase.auth().signInWithPhoneNumber()
    // }

    const requestEmail = (email: string) => {
        firebase.auth().sendSignInLinkToEmail(email, {
            url: 'http://localhost:3001/',
            handleCodeInApp: true,
        }).then(() => {
            localStorage.setItem('emailSent', email)
            onMessage(`Sent email to "${email}".`)
        })
    }

    return {signin, requestEmail}
}

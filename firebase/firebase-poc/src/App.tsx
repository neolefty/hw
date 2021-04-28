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
    const auth = useProviderAuth()
    const [email, setEmail] = useState<string>('')
    const [error, setError] = useState<Error>()
    const [user, setUser] = useState<firebase.User | null>(null)
    const [message, setMessage] = useState<string>()
    const url = window.location.toString()
    const isSignin = firebase.auth().isSignInWithEmailLink(url)
    useEffect(() => {
        if (isSignin && !email) {
            const storedEmail = localStorage.getItem('emailSent')
            if (typeof storedEmail === 'string')
                setEmail(storedEmail)
        }
    }, [email, isSignin])
    useEffect(() => {
        firebase.auth().onAuthStateChanged(setUser)
    }, [])
    const handleSignIn = useCallback(() => {
        if (email) {
            try {
                auth.signin(email, url)
                localStorage.removeItem('emailSent')
            }
            catch(e) {
                setError(e)
            }
        }
    }, [auth, email, url])
    return (
        <>
            <div>Firebase POC: {app.name}</div>
            <div>Email: <input value={email} onChange={e => setEmail(e.target.value)}/></div>
            <div>
                {!isSignin && <button onClick={() => email && auth.requestEmail(email)} disabled={!email}>Send Link</button>}
                {isSignin && <button onClick={handleSignIn} disabled={!email || !!auth.user}>Sign In</button>}
            </div>
            {auth.sent && <div>Sent email to <strong>{auth.sent}</strong></div>}
            {auth.user && <div>Signed in just now as <strong>{auth.user.email}</strong></div>}
            {user &&
                <div>
                    <strong>Really</strong> signed in as {user.displayName || user.email || user.phoneNumber}
                    {user?.photoURL && <img src={user.photoURL} alt={user.displayName || 'photo'}/>}
                    <button onClick={() => firebase.auth().signOut().then(() => setMessage('Signed out'))}>Sign Out</button>
                </div>
            }
            {message && <div style={{background: '#cfc', padding: '1em'}}>{message}</div>}
            {error && <div style={{background: '#fcc', padding: '1em'}}>{error.message}</div>}
        </>
    )
}

interface ProviderAuth {
    requestEmail: (email: string) => void
    signin: (email: string, link: string) => void
    user: firebase.User | null
    sent?: string
}

const useProviderAuth = (): ProviderAuth => {
    const [user, setUser] = useState<firebase.User  | null>(null)
    const [sent, setSent] = useState<string>()

    const signin = (email: string, link: string) => {
        if (!firebase.auth().isSignInWithEmailLink(link))
            throw new Error(`Not a signing link: ${link}`)
        firebase.auth().signInWithEmailLink(email, link).then(
            // TODO find the tutorial this came from
            // TODO consider using react-firebase-hooks — https://www.npmtrends.com/react-firebase-hooks-vs-reactfire
            // TODO store the user for later retrieval — what's the firebase way to do that?
            response => setUser(response.user)
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
            setSent(email)
        })
    }

    return {user, sent, signin, requestEmail}
}

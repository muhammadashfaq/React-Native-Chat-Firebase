import React from 'react';
import { createStackNavigator } from 'react-navigation-stack'
import {createSwitchNavigator,createAppContainer} from 'react-navigation'


import Login from '../screens/login'
import Signup from '../screens/signup'
import Home from '../screens/home'
import Chat from '../screens/chat'


const authStack = createStackNavigator({
    Login: Login,
    Signup: Signup
},{
    initialRouteName: 'Login'
})

const appStack = createStackNavigator({
    Home: Home,
    Chat: Chat
},{
    initialRouteName: 'Home',
})

const mainNav = createSwitchNavigator({
    Auth: authStack,
    App: appStack
},{
    initialRouteName: 'Auth'
})

export default createAppContainer(mainNav)



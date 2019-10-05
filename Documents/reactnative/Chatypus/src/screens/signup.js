import React, {Component} from 'react';
import {
  StatusBar,
  KeyboardAvoidingView,
  Alert,
  Text,
  TextInput,
  TouchableHighlight,
  View,
} from 'react-native';
import styles from '../utils/styles';
import firebaseService from '../service/firebase-service';

class SignUp extends Component {
  static navigationOptions = {
    headerTitle: 'Register',
    headerStyle: {
      backgroundColor: '#1E90FF',
    },
    headerTintColor: 'white',
  };

  constructor(props) {
    super(props);
    this.state = {
      email: '',
      password: '',
    };
  }

  signUp = async () => {
    const {email, password} = this.state;
    if (email !== '' && password !== '') {
      try {
        await firebaseService
          .auth()
          .createUserWithEmailAndPassword(email, password);
        this.props.navigation.navigate('Login');
      } catch (err) {
        alert(err);
      }
    } else {
      Alert.alert(
        'Invalid Sign Up',
        'The Email and Password fields cannot be blank.',
        [{text: 'OK', onPress: () => console.log('OK Pressed')}],
        {cancelable: false},
      );
    }
  };

  render() {
    return (
      <KeyboardAvoidingView
        style={styles.keyboardView}
        contentContainerStyle={styles.authContainer}
        behavior={'position'}>
        <StatusBar barStyle="light-content" />
        <Text style={styles.appTitle}>Chatypus!</Text>
        <Text style={styles.authInputLabel}>Email</Text>
        <TextInput
          style={styles.authTextInput}
          autoCapitalize={'none'}
          keyboardType={'email-address'}
          placeholder={'Example@email.com'}
          placeholderTextColor={'#fff'}
          onChangeText={text => this.setState({email: text})}
        />
        <Text style={styles.authInputLabel}>Password</Text>
        <TextInput
          secureTextEntry={true}
          style={styles.authTextInput}
          placeholder={'Password'}
          placeholderTextColor={'#fff'}
          onChangeText={text => this.setState({password: text})}
        />
        <TouchableHighlight
          style={styles.authButton}
          underlayColor={'#1E90FF'}
          onPress={() => this.signUp()}>
          <Text style={styles.authButtonText}>Sign Up</Text>
        </TouchableHighlight>
        <TouchableHighlight
          underlayColor={'#1E90FF'}
          onPress={() => this.props.navigation.navigate('Login')}>
          <Text style={styles.authLowerText}>Go to Sign In</Text>
        </TouchableHighlight>
      </KeyboardAvoidingView>
    );
  }
}

export default SignUp;

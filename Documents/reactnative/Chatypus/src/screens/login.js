import React, {Component} from 'react';
import {
  StatusBar,
  KeyboardAvoidingView,
  Alert,
  Text,
  TextInput,
  TouchableHighlight,
  View
} from 'react-native';
import styles from '../utils/styles';
import {SocialIcon} from 'react-native-elements';
import firebaseService from '../service/firebase-service';

class Login extends Component {
  static navigationOptions = {
    headerTitle: 'Login',
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

  componentDidMount = async () => {
    try {
      await firebaseService.auth().onAuthStateChanged(user => {
        if (user) {
          this.props.navigation.navigate('Home');
        }
      });
    } catch (err) {
      alert(err);
    }
  };

  signIn = async () => {
    const {email, password} = this.state;
    if (email !== '' && password !== '') {
      try {
        await firebaseService
          .auth()
          .signInWithEmailAndPassword(email, password);
        console.log(this.state.email + ' logged in');
        this.props.navigation.navigate('Home');
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
          onPress={() => this.signIn()}>
          <Text style={styles.authButtonText}>Sign In</Text>
        </TouchableHighlight>
        <TouchableHighlight
          underlayColor={'#1E90FF'}
          onPress={() => this.props.navigation.navigate('Signup')}>
          <Text style={styles.authLowerText}>Go to Sign Up</Text>
        </TouchableHighlight>
        <View style={{justifyContent: 'space-between',flexDirection: 'row',marginTop: 20}}>
            <SocialIcon type={'facebook'} raised/>
            <SocialIcon type={'twitter'} raised/>
            <SocialIcon type={'instagram'} raised/>
        </View>
      </KeyboardAvoidingView>
    );
  }
}

export default Login;

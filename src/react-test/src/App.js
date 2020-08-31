import React, {useState} from 'react';
import Email from './Email';

function App() {

  return(
    

    <div className = "app">
      <h1>StraightShot</h1>
      <Email from = "Nathan Cheshire" content = "I woke up in" attachements = "Chris_Brown.png"/>
      <Email from = "Mallory Duke" content = "I woke up in" attachements = "Chris_Brown.png"/>
      <Email from = "Sam Boggs" content = "I woke up in" attachements = "Chris_Brown.png"/>
      <Email from = "Chadwick Boseman" content = "I woke up in" attachements = "Chris_Brown.png"/>
    </div>
  );
}

export default App;
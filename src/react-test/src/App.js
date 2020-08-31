import React from 'react';

function App() {

  const hello = () => {
    console.log("Hello");
  };

const counter = 15;

  return(
    <div>
      <h1>Hello React</h1>
  <button onClick={hello()}>{counter}</button>
    </div>
  );
}

export default App;
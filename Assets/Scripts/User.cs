using UnityEngine;

public class User : MonoBehaviour
{
    private Rigidbody2D _rigidbody2D;
    [SerializeField] private float moveSpeed;
    [SerializeField] private GameObject bullet;
    [SerializeField] private float bulletSpeed;
    [SerializeField] private Joystick joyStick;
    private void Start()
    {
        _rigidbody2D = GetComponent<Rigidbody2D>();
    }

    public void Fire()
    {
        Instantiate(bullet, transform.position, Quaternion.identity).GetComponent<Rigidbody2D>().AddForce(_rigidbody2D.velocity.normalized * bulletSpeed);
    }

    private void Update()
    {
        _rigidbody2D.AddForce(joyStick.Direction* moveSpeed);
        /*
        if (Input.GetKey(KeyCode.A))
            _rigidbody2D.AddForce(Vector2.left * moveSpeed);
        if (Input.GetKey(KeyCode.S))
            _rigidbody2D.AddForce(Vector2.down * moveSpeed);
        if (Input.GetKey(KeyCode.D))
            _rigidbody2D.AddForce(Vector2.right * moveSpeed);
        if (Input.GetKey(KeyCode.W))
            _rigidbody2D.AddForce(Vector2.up * moveSpeed);
        if (Input.GetKeyDown(KeyCode.Space))
            Instantiate(bullet, transform.position, Quaternion.identity).GetComponent<Rigidbody2D>().AddForce(_rigidbody2D.velocity.normalized * bulletSpeed);
        */

    }
}
